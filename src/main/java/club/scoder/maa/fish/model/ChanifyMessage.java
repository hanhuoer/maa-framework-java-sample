package club.scoder.maa.fish.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class ChanifyMessage {

    private String api;
    private String token;
    private String title;
    private String text;
    private String copy;
    @Builder.Default
    private int autoCopy = 0;
    @Builder.Default
    private int sound = 1;
    @Builder.Default
    private int priority = 10;

}
