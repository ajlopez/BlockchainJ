package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.net.MessageChannel;
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
    private BlockingQueue<MessageTask> messageTaskNormalQueue = new LinkedBlockingDeque<>();
    private BlockingQueue<MessageTask> messageTaskPriorityQueue = new LinkedBlockingDeque<>();
    private boolean stopped = false;
    private List<Runnable> emptyActions = new ArrayList<>();

    public ReceiveProcessor(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    public void start() {
        new Thread(() -> { this.processQueue(this.messageTaskPriorityQueue); }).start();
        new Thread(() -> { this.processQueue(this.messageTaskNormalQueue); }).start();
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
        if (!this.messageTaskNormalQueue.isEmpty())
            return;

        if (!this.messageTaskPriorityQueue.isEmpty())
            return;

        emitEmpty();
    }

    public void postMessage(Peer sender, Message message) {
        if (message.isPriorityMessage())
            this.messageTaskPriorityQueue.add(new MessageTask(message, sender));
        else
            this.messageTaskNormalQueue.add(new MessageTask(message, sender));
    }

    public void onEmpty(Runnable action) {
        this.emptyActions.add(action);
    }

    private void emitEmpty() {
        this.emptyActions.forEach(a -> a.run());
    }
}
