package interviewquestions.ls;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;

class ActorScore {
    public String name;
    public Integer year;
    public Integer score;

    public ActorScore(String line) {
        String[] items = line.split(",");
        name = items[0];
        year = Integer.valueOf(items[1]);
        score = Integer.valueOf(items[2]);
    }

    @Override
    public String toString() {
        return year + " : " + name + " : " + score;
    }
}

public class Main {
    public static void main(String[] args) throws IOException {
        File dataFile = Paths.get("./src/main/java/interviewquestions/ls/data.txt").toFile();
        var res = FileUtils.readLines(dataFile)
                .stream()
                .map((ActorScore::new))
                .filter(actorScore -> actorScore.score > 0)
                .collect(Collectors.groupingBy(actorScore -> actorScore.year))
                .values()
                .stream()
                .peek(System.out::println)
                .map(listByYear ->
                        listByYear.stream()
                                .peek(System.out::println)
                                .sorted(((o1, o2) -> o2.score - o1.score))
                                .limit(1)
                                .collect(Collectors.toList())
                ).limit(2)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        System.out.println("============================");
        System.out.println(res);
    }
}
