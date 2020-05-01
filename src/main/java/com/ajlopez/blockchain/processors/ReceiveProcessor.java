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
        new Thread(() -> { this.processPriorityQueue(); }).start();
        new Thread(() -> { this.processNormalQueue(); }).start();
    }

    public void stop() {
        this.stopped = true;
    }

    public void processNormalQueue() {
        while (!this.stopped) {
            try {
                MessageTask task = this.messageTaskNormalQueue.poll(1, TimeUnit.SECONDS);

                if (task != null)
                    this.messageProcessor.processMessage(task.getMessage(), task.getSender());
                else {
                    if (this.messageTaskPriorityQueue.isEmpty())
                        emitEmpty();

                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void processPriorityQueue() {
        while (!this.stopped) {
            try {
                MessageTask task = this.messageTaskPriorityQueue.poll(1, TimeUnit.SECONDS);

                if (task != null)
                    this.messageProcessor.processMessage(task.getMessage(), task.getSender());
                else {
                    if (this.messageTaskNormalQueue.isEmpty())
                        emitEmpty();

                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
