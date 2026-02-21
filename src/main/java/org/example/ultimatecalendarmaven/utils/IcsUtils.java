package org.example.ultimatecalendarmaven.utils;

import org.example.ultimatecalendarmaven.model.Appointment;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for generating iCalendar (RFC 5545) content from an {@link Appointment}.
 */
public final class IcsUtils {

    private static final DateTimeFormatter ICS_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC);

    private IcsUtils() {}

    /**
     * Builds a VCALENDAR string with a single VEVENT representing the given appointment.
     *
     * @param appointment the appointment to convert
     * @return the iCalendar content as a {@code String}
     */
    public static String buildIcs(Appointment appointment) {
        String uid       = appointment.getId() + "@ultimate-calendar";
        String dtStamp   = ICS_FORMAT.format(appointment.getCreatedAt());
        String dtStart   = ICS_FORMAT.format(appointment.getStartsAt());
        String dtEnd     = ICS_FORMAT.format(appointment.getEndsAt());
        String summary   = escape(appointment.getService().getName());
        String staffName = escape(appointment.getStaff().getName());

        return "BEGIN:VCALENDAR\r\n"
                + "VERSION:2.0\r\n"
                + "PRODID:-//Ultimate Calendar//Appointment//EN\r\n"
                + "METHOD:REQUEST\r\n"
                + "BEGIN:VEVENT\r\n"
                + "UID:" + uid + "\r\n"
                + "DTSTAMP:" + dtStamp + "\r\n"
                + "DTSTART:" + dtStart + "\r\n"
                + "DTEND:" + dtEnd + "\r\n"
                + "SUMMARY:" + summary + "\r\n"
                + "DESCRIPTION:Cita con " + staffName + "\r\n"
                + "END:VEVENT\r\n"
                + "END:VCALENDAR\r\n";
    }

    /** Escapes special characters as required by RFC 5545. */
    private static String escape(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace(";", "\\;")
                   .replace(",", "\\,")
                   .replace("\n", "\\n");
    }
}
