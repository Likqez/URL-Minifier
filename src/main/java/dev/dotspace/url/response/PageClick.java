package dev.dotspace.url.response;

import java.sql.Timestamp;

public record PageClick(String address,
                        String userAgent,
                        String region,
                        Timestamp accessTime) {
}
