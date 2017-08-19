package com.vsubhuman.telegram;

import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.TelegramWebhookBot;

import java.util.function.Function;

public class TelegramFactory {

	public static AbsSender sender(String token, String username) {
		return webhookBot(token, username, x -> null);
	}

	public static TelegramWebhookBot webhookBot(
			String token, String username, Function<Update, BotApiMethod> onUpdate) {
		return webhookBot(token, username, username, onUpdate);
	}

	public static TelegramWebhookBot webhookBot(
			String token, String username, String path, Function<Update, BotApiMethod> onUpdate) {
		return new TelegramWebhookBot() {
			@Override
			public String getBotToken() {
				return token;
			}

			@Override
			public BotApiMethod onWebhookUpdateReceived(Update update) {
				return onUpdate.apply(update);
			}

			@Override
			public String getBotUsername() {
				return username;
			}

			@Override
			public String getBotPath() {
				return path;
			}
		};
	}
}
