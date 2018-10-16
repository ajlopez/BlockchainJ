package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.net.InputChannel;
import com.ajlopez.blockchain.net.Peer;
import com.ajlopez.blockchain.net.messages.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Created by ajlopez on 28/01/2018.
 */
public class InputProcessor implements Runnable, InputChannel {
    private MessageProcessor messageProcessor;
    private BlockingQueue<MessageTask> messageTaskQueue = new LinkedBlockingDeque<>();
    private boolean stopped = false;
    private List<Runnable> emptyActions = new ArrayList<>();

    public InputProcessor(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    public void start() {
        new Thread(this).start();
    }

    public void stop() {
        this.stopped = true;
    }

    public void run() {
        while (!this.stopped) {
            try {
                MessageTask task = this.messageTaskQueue.poll(1, TimeUnit.SECONDS);

                if (task != null)
                    this.messageProcessor.processMessage(task.getMessage(), task.getSender());
                else {
                    emitEmpty();
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void postMessage(Peer sender, Message message) {
        this.messageTaskQueue.add(new MessageTask(message, sender));
    }

    public void onEmpty(Runnable action) {
        this.emptyActions.add(action);
    }

    private void emitEmpty() {
        this.emptyActions.forEach(a -> a.run());
    }
}
