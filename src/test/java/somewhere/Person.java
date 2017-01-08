package somewhere;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
class Person {
    private int id;
    private String name;
    private String description;

    public static Person fromCsv(String line) {
        String[] parts = line.split(",");
        if (parts.length > 2) {
            return new Person(Integer.valueOf(parts[0]), parts[1], parts[2]);
        } else {
            return new Person(Integer.valueOf(parts[0]), parts[1], null);
        }
    }

}
