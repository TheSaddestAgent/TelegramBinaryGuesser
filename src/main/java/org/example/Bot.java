package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    private int l = 0, r = 1000, mid = 0;
    private final String name;
    private final String token;

    public Bot(final String botName, final String botToken) {
        super();
        this.name = botName;
        this.token = botToken;
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }
    boolean setBorders = false;

    @Override
    public void onUpdateReceived(final Update update) {
        final Message message = update.getMessage();
        final Long chatId = message.getChatId();
        String answer = message.getText();
        this.mid = (this.l + this.r) / 2;
        if(this.r - this.l == 0) {
            sendMsg(chatId.toString(), "Our final number is " + l + " !\nThank you for using this bot");
            sendMsg(chatId.toString(), "Set up borders please:");
            this.l = 0; this. r = 1000;
            setBorders = false;
            return;
        }

        if(answer.equals("/start")){
            sendMsg(chatId.toString(), "Set up borders please:");
        } else {
            if(!setBorders){
            String[] borders = answer.split(" ");
            String strL = borders[0];
            String strR = borders[1];
            int borderL = Integer.parseInt(strL);
            int borderR = Integer.parseInt(strR);
            this.l = borderL;
            this.r = borderR;
            if(l > r){
                int tmp = l;
                this.l = r;
                this.r = tmp;
            }
            mid = (l + r) / 2;
            sendMsg(chatId.toString(), "Our number is " + mid + "\nYour number is...");
            setBorders = true;
            }
        }
        if (answer.equals("Lower")) {
            this.r = this.mid;
            this.mid = (l + r) / 2;
            sendMsg(chatId.toString(), "Our number is " + mid + "\nYour number is...");
            return;
        }
        if (answer.equals("Higher")) {
            this.l = this.mid;
            this.mid = (l + r) / 2;
            sendMsg(chatId.toString(), "Our number is " + mid + "\nYour number is...");
            return;
        }
        if (answer.equals("Correct")){
            sendMsg(chatId.toString(), "Great job, " + message.getFrom().getFirstName() + "!\nI hope you enjoyed!");
            sendMsg(chatId.toString(), "Set up borders please:");
            setBorders = false;
            return;
        }
        if (answer.equals("Restart")){
            sendMsg(chatId.toString(), "Bot has been restarted!\n");
            setBorders = false;
            sendMsg(chatId.toString(), "Set up borders please:");
            return;
        }
    }

    public synchronized void sendMsg(String chatId, String s) {
        SendMessage answer = new SendMessage();
        answer.enableMarkdown(true);
        answer.setText(s);
        answer.setChatId(chatId);
        setButtons(answer);
        try {
            this.execute(answer);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void setButtons(SendMessage sendMessage) {
        // Create a keyboard
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Create a list of keyboard rows
        List<KeyboardRow> keyboard = new ArrayList<>();

        // First keyboard row
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Add buttons to the first keyboard row
        keyboardFirstRow.add(new KeyboardButton("Lower"));

        // Second keyboard row
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        // Add the buttons to the second keyboard row
        keyboardSecondRow.add(new KeyboardButton("Higher"));

        // Third keyboard row
        KeyboardRow keyboardThirdRow = new KeyboardRow();
        keyboardThirdRow.add(new KeyboardButton("Correct"));

        KeyboardRow keyboardRestartRow = new KeyboardRow();
        keyboardRestartRow.add(new KeyboardButton("Restart"));
        // Add all the keyboard rows to the list
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        keyboard.add(keyboardThirdRow);
        keyboard.add(keyboardRestartRow);
        // and assign this list to our keyboard
        replyKeyboardMarkup.setKeyboard(keyboard);
    }
}