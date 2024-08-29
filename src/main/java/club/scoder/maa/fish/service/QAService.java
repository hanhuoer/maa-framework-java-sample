package club.scoder.maa.fish.service;

import io.github.hanhuoer.maa.util.FileUtils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class QAService {

    private static final Pattern patternHz = Pattern.compile("[\u4e00-\u9fa5]");
    private List<String> questionList = new ArrayList<>();
    private List<Question> questionObjList = new ArrayList<>();
    private Map<String, String> qaMap = new HashMap<>();
    private Map<String, Question> qaObjMap = new HashMap<>();


    public QAService() {
    }

    public static void main(String[] args) {
        QAService service = new QAService();
        service.init();
        String match = service.match("印度唐僧成功收拾了黑山老妖？福");

        System.out.println(match);
    }

    @PostConstruct
    public void init() {
        this.loadQA();
    }

    public void loadQA() {
        String filePath = FileUtils.joinUserDir("resources", "resource", "qa.txt").getAbsolutePath();

        List<String> questions = new ArrayList<>();
        List<String> answers = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
                if (i % 2 == 0) questions.add(line);
                else answers.add(line);
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (questions.size() != answers.size()) {
            // 忽略本次加载
            return;
        }

        qaMap.clear();
        qaObjMap.clear();
        questionObjList.clear();

        for (int i = 0; i < questions.size(); i++) {
            String question = questions.get(i);
            String answer = answers.get(i);

            questionList.add(question);
            qaMap.put(question, answer);

            Question e = new Question(question, answer);
            qaObjMap.put(question, e);
            questionObjList.add(e);
        }

        // 加载成功
        System.out.println("题库加载成功");
    }

    public String match(String ocrStr) {
        // 匹配题库
        String similarQuestion = findMostSimilar(ocrStr, questionList);
        String similarAnswer = qaMap.get(similarQuestion);
        Question question = new Question(similarQuestion, similarAnswer);
        question.setRawQuestion(ocrStr);

        if (question.ifEmpty()) {
            return "";
        } else if (question.ifBlank()) {
            return "";
        } else if (question.hasNotHz()) {
            return "";
        }

        String answer = question.getAnswer();
        log.info("[答题] 答案: {}, 题目: {}", question.getAnswer(), question.getQuestion());
        return answer;
    }

    private String findMostSimilar(String target, List<String> stringList) {
        target = target.replace("\n", "");
        target = target.replace(" ", "");

        int minDistance = Integer.MAX_VALUE;
        String mostSimilarString = null;

        for (String string : stringList) {
            int distance = StringUtils.getLevenshteinDistance(target, string);
            if (distance < minDistance) {
                minDistance = distance;
                mostSimilarString = string;
            }
        }

//        System.out.println("The most similar string to '" + target + "' is: " + mostSimilarString);
        return mostSimilarString;
    }

    @Data
    public static class Question {

        String cutImg;
        String originImg;
        private String rawQuestion;
        private String question;
        private String answer;

        public Question(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }

        public boolean ifEmpty() {
            return StringUtils.isEmpty(rawQuestion);
        }

        public boolean ifNotEmpty() {
            return !ifEmpty();
        }

        public boolean ifBlank() {
            return StringUtils.isBlank(rawQuestion);
        }

        public boolean ifNotBlank() {
            return !ifBlank();
        }

        public boolean hasHz() {
            Matcher matcher = patternHz.matcher(rawQuestion);
            return matcher.find();
        }

        public boolean hasNotHz() {
            return !hasHz();
        }

        @Override
        public int hashCode() {
            return question.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Question) {
                Question question = (Question) obj;
                return this.question.equals(question.getQuestion());
            }
            return false;
        }
    }

    @Data
    public static class Context {

        private Set<Question> questions = new HashSet<>();
        private int roundTimes = 0;
        private Boolean ifPlaying = false;
        private int energyFailedTimes = 0;

        public void newRound() {
            this.roundTimes++;
            started();
            energyFailedTimes = 0;
            questions.clear();
        }

        public void started() {
            ifPlaying = true;
        }

        public void over() {
            ifPlaying = false;
        }

        public void increaseEnergyTimes() {
            this.energyFailedTimes++;
        }

        public boolean checkIfOverEnergyTimes() {
            return this.energyFailedTimes > 3;
        }
    }

}
