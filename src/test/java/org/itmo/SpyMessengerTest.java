package org.itmo;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.TimeUnit;

class SpyMessengerTest {

    @Test
    void testMessageSelfDestructionAfterReading() {
        SpyMessenger messenger = new SpyMessenger();
        messenger.sendMessage("Alice", "Bob", "Top Secret", "1234");

        // Первое чтение — сообщение должно быть доступно
        assertEquals("Top Secret", messenger.readMessage("Bob", "1234"));

        // Второе чтение — сообщение уже должно исчезнуть
        assertNull(messenger.readMessage("Bob", "1234"));
    }

    @Test
    void testMessageDeletionAfterTimeout() throws InterruptedException {
        SpyMessenger messenger = new SpyMessenger();
        messenger.sendMessage("Alice", "Bob", "Self-Destruct", "1234");

        // Ждем 1.6 секунды (дольше, чем 1.5 секунды)
        TimeUnit.MILLISECONDS.sleep(1600);

        // Сообщение должно быть удалено
        assertNull(messenger.readMessage("Bob", "1234"));
    }

    @Test
    void testUserInactivityDeletion() throws InterruptedException {
        SpyMessenger messenger = new SpyMessenger();
        messenger.sendMessage("Alice", "Bob", "Message 1", "1234");
        messenger.sendMessage("Alice", "Bob", "Message 2", "1234");

        // Ждем 3,1 секунды (больше, чем 3 секунды)
        TimeUnit.MILLISECONDS.sleep(3100);

        // Проверяем, что все сообщения удалены
        assertNull(messenger.readMessage("Bob", "1234"));
    }

    @Test
    void testWrongPasscode() {
        SpyMessenger messenger = new SpyMessenger();
        messenger.sendMessage("Alice", "Bob", "Secret", "1234");

        assertNull(messenger.readMessage("Bob", "0000"));

        // Проверяем, что сообщение удалено после попытки взлома
        assertNull(messenger.readMessage("Bob", "1234"));
    }

    @Test
    void testMessageLimit() {
        SpyMessenger messenger = new SpyMessenger();
        for (int i = 1; i <= 5; i++) {
            messenger.sendMessage("Alice", "Bob", "Message " + i, "1234");
        }
        // Отправляем шестое сообщение
        messenger.sendMessage("Alice", "Bob", "Message 6", "1234");
        assertNull(messenger.readMessage("Bob", "1234"));
        // Проверяем, что остальные сообщения на месте
        for (int i = 2; i <= 6; i++) {
            assertEquals("Message " + i, messenger.readMessage("Bob", "1234"));
        }
    }
}