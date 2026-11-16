package ru.zhanbyrbay.zhanvisuals;

public class AntiSpam {

    private static String lastMessage = "";
    private static long lastSendTime = 0;
    private static final long COOLDOWN = 1000L; // 1 секунда задержки

    public static boolean canSendMessage(String message) {
        if (!ZhanVisuals.antiSpam) return true;
        
        long now = System.currentTimeMillis();

        if (message.equals(lastMessage) && (now - lastSendTime) < COOLDOWN) {
            return false; // Блокируем спам
        }

        lastMessage = message;
        lastSendTime = now;
        return true;
    }
}
