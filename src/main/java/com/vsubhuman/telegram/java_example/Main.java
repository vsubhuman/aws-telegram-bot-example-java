package com.vsubhuman.telegram.java_example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

import java.io.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

import static com.vsubhuman.telegram.TelegramFactory.sender;
import static java.lang.System.getenv;

public class Main implements RequestStreamHandler {

	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final AbsSender SENDER = sender(getenv("bot_token"), getenv("bot_username"));
	private static final String VERSION = readVersion();

	public Main() {
		System.out.println("AwsTelegramBot version: " + VERSION);
	}

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		Update update;
		try {
			update = MAPPER.readValue(input, Update.class);
			System.out.println("Update @ '" + getFormattedTimestamp(update) + "' : " + update);
		} catch (Exception e) {
			System.err.println("Failed to parse update: " + e);
			throw new RuntimeException("Failed to parse update!", e);
		}
		System.out.println("Starting handling update " + update.getUpdateId());
		try {
			handleUpdate(update);
		} catch (Exception e) {
			System.err.println("Failed to handle update: " + e);
			throw new RuntimeException("Failed to handle update!", e);
		}
        System.out.println("Finished handling update " + update.getUpdateId());
    }

	private void handleUpdate(Update update) throws Exception {
		if (update.getMessage() == null) {
			return;
		}
		String text = update.getMessage().getText().toLowerCase();
		String responseText;
		if (text.equals("/version")) {
			responseText = "Version: " + VERSION;
		} else {
			responseText = "Echo> " + text;
		}
		SendMessage sendMessage = new SendMessage()
				.setChatId(update.getMessage().getChatId())
				.setText(responseText);
		System.out.println("Sending message: " + sendMessage);
		try {
			Message message = SENDER.sendMessage(sendMessage);
			System.out.println("Message sent: " + message);
		} catch (Exception e) {
			System.err.println("Failed to send mesage: " + e);
			throw new RuntimeException("Failed to send message!", e);
		}
	}

	private static String getFormattedTimestamp(Update update) {
		if (update.getMessage() != null) {
			Instant instant = Instant.ofEpochSecond(update.getMessage().getDate());
			return DateTimeFormatter.ISO_INSTANT.format(instant);
		}
		return "<unknown date>";
	}

	private static String readVersion() {
		try (InputStream in = Main.class.getResourceAsStream("/version");
			 BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			return reader.readLine();
		} catch (IOException e) {
			System.err.println("Failed to read version: " + e);
			return "<unknown>";
		}
	}
}
