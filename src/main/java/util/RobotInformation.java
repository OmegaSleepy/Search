package util;

import java.util.List;

public record RobotInformation(List<String> bannedUrl, long millisecondTimeout) {}
