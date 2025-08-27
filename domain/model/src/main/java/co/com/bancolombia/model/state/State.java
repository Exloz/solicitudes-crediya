package co.com.bancolombia.model.state;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class State {
    private Long id;
    private String name;
    private String description;
}
