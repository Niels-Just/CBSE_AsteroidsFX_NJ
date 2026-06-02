package dk.sdu.cbse.scoring;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/scores")
public class ScoreController {

    private final List<Integer> scores = Collections.synchronizedList(new ArrayList<>());

    @GetMapping
    public List<Integer> getAll() {
        return scores;
    }

    @PostMapping
    public void add(@RequestBody Integer score) {
        scores.add(score);
    }

    // Lets the game send a score in the web address itself.
    @PostMapping("/submit/{score}")
    public void addByPath(@PathVariable("score") Integer score) {
        scores.add(score);
    }

    // Returns the high score as plain text so it's easy to read back.
    @GetMapping(value = "/highscore", produces = MediaType.TEXT_PLAIN_VALUE)
    public String highScore() {
        synchronized (scores) {
            return String.valueOf(scores.stream().max(Integer::compareTo).orElse(0));
        }
    }
}
