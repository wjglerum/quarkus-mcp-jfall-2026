package nl.lunatech.porto.api.clock;

import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * The venue's time zone. Postgres hands timestamps back in UTC; session times leave the API in
 * venue time so an AI client reads "08:00+01:00" and does not announce the talk at 07:00.
 */
public final class Venue {

    public static final ZoneId ZONE = ZoneId.of("Europe/Amsterdam");

    private Venue() {
    }

    public static OffsetDateTime local(OffsetDateTime t) {
        return t == null ? null : t.atZoneSameInstant(ZONE).toOffsetDateTime();
    }
}
