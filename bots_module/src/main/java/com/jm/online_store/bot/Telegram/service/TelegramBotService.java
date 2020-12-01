package com.jm.online_store.bot.Telegram.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

/**
 * The interface Telegram bot service.
 */
public interface TelegramBotService {

    /**
     * Gets some quantity of news.
     *
     *
     * @return the some quantity of news
     */
    String getSomeQuantityOfNews();

    /**
     * Gets actual stocks.
     *
     * @return the actual stocks
     */
    String getActualStocks();

    /**
     * Gets repair order.
     *
     * @param orderNumber the order number
     * @return the repair order
     */
    String getRepairOrder(String orderNumber);

    /**
     * Gets hello message.
     *
     * @return the hello message
     */
    String getHelloMessage();

    /**
     * Gets default message.
     *
     * @return the default message
     */
    String getDefaultMessage();

    /**
     * Gets help message.
     *
     * @return the help message
     */
    String getHelpMessage();


    /**
     * Gets main menu keyboard.
     *
     * @return the main menu keyboard
     */
    ReplyKeyboardMarkup getMainMenuKeyboard();

}
