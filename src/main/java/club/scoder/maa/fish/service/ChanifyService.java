package club.scoder.maa.fish.service;

import club.scoder.maa.fish.model.ChanifyMessage;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChanifyService {

    @Autowired
    private OkHttpClient okHttpClient;

    public static void main(String[] args) {

    }

    public void send(String message) {
        this.send(ChanifyMessage.builder()
                .api("http://xxx/v1/sender")
                .token("xxx")
                .title(null)
                .text(message)
                .copy(null)
                .autoCopy(0)
                .sound(1)
                .priority(10)
                .build());
    }

    public void send(String server, String token, String content) {
        this.send(ChanifyMessage.builder()
                .api(server + "/v1/sender")
                .token(token)
                .title("test")
                .text(content)
                .copy(null)
                .autoCopy(0)
                .sound(1)
                .priority(10)
                .build());
    }

    public boolean send(ChanifyMessage message) {
        if (message == null) {
            return false;
        }

        String url = message.getApi();
        JSONObject json = new JSONObject();

        json.put("token", message.getToken());
        json.put("title", message.getTitle());
        json.put("text", message.getText());
        json.put("copy", message.getCopy());
        json.put("autoCopy", message.getAutoCopy());
        json.put("sound", message.getSound());
        json.put("priority", message.getPriority());


        // 构建请求
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), json.toString()))
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            ResponseBody responseBody = response.body();

            if (response.isSuccessful()) {
                if (responseBody != null) {
                    String responseData = responseBody.string();
                    log.debug("[Chanify] response: {}", responseData);
                }
            } else {
//                System.err.println("Request failed: " + statusCode + " - " + response.message());
            }
            return true;
        } catch (IOException e) {
//            e.printStackTrace();
            return false;
        }
    }
}
