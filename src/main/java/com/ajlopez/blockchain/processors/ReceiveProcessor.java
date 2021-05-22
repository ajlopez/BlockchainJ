package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.net.MessageChannel;
import com.ajlopez.blockchain.net.messages.MessageType;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.net.messages.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Created by ajlopez on 28/01/2018.
 */
public class ReceiveProcessor implements MessageChannel {
    private MessageProcessor messageProcessor;
    private BlockingQueue<MessageTask> messageTaskBlocksAndStatusQueue = new LinkedBlockingDeque<>();
    private BlockingQueue<MessageTask> messageTaskTransactionsQueue = new LinkedBlockingDeque<>();
    private BlockingQueue<MessageTask> messageTaskOthersQueue = new LinkedBlockingDeque<>();
    private boolean stopped = false;
    private List<Runnable> emptyActions = new ArrayList<>();

    public ReceiveProcessor(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    public void start() {
        new Thread(() -> { this.processQueue(this.messageTaskBlocksAndStatusQueue); }).start();
        new Thread(() -> { this.processQueue(this.messageTaskTransactionsQueue); }).start();
        new Thread(() -> { this.processQueue(this.messageTaskOthersQueue); }).start();
    }

    public void stop() {
        this.stopped = true;
    }

    public void processQueue(BlockingQueue<MessageTask> messageTaskQueue) {
        while (!this.stopped) {
            try {
                MessageTask task = messageTaskQueue.poll(1, TimeUnit.SECONDS);

                if (task != null)
                    this.messageProcessor.processMessage(task.getMessage(), task.getSender());
                else {
                    checkIsEmpty();

                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkIsEmpty() {
        if (!this.messageTaskBlocksAndStatusQueue.isEmpty())
            return;

        if (!this.messageTaskTransactionsQueue.isEmpty())
            return;

        if (!this.messageTaskOthersQueue.isEmpty())
            return;

        emitEmpty();
    }

    public void postMessage(Peer sender, Message message) {
        MessageTask messageTask = new MessageTask(message, sender);
        MessageType messageType = message.getMessageType();

        if (messageType == MessageType.BLOCK || messageType == MessageType.STATUS)
            this.messageTaskBlocksAndStatusQueue.add(messageTask);
        else if (messageType == MessageType.TRANSACTION)
            this.messageTaskTransactionsQueue.add(messageTask);
        else
            this.messageTaskOthersQueue.add(messageTask);
    }

    public void onEmpty(Runnable action) {
        this.emptyActions.add(action);
    }

    private void emitEmpty() {
        this.emptyActions.forEach(a -> a.run());
    }
}
