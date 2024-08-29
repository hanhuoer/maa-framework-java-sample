package club.scoder.maa.fish.controller;

import io.github.hanhuoer.maa.core.AdbController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/maa")
@RequiredArgsConstructor
public class MaaController {

    @GetMapping("/api-find")
    public Object adbFind() {
        return AdbController.find();
    }

}
