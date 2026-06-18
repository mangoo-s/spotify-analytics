package shared.exceptions;

import java.time.Instant;

public record ErrorResponse(String message,
                            Instant time) {
}
