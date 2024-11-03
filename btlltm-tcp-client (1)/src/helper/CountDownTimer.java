package helper;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CountDownTimer {
    
    int timeLimit; // thời gian tối đa của đồng hồ đếm ngược
    int currentTick; // thời gian còn lại của đồng hồ đếm ngược
    int tickInterval; // Khoảng thời gian (tính bằng giây) giữa các lần gọi callback tick.
    Timer timer; // Đối tượng Timer để lên lịch và thực hiện các tác vụ định kỳ.
    ExecutorService executor; //ExecutorService để quản lý các thread chạy các callback.

    boolean isPaused = false; //Cờ để kiểm tra xem đồng hồ đếm ngược có đang bị tạm dừng không.

    public CountDownTimer(int _timeLimit) {
        timeLimit = _timeLimit;
        currentTick = _timeLimit;
        tickInterval = 2; // default is 1
        timer = new Timer();
        executor = Executors.newFixedThreadPool(2);
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    public void restart() {
        currentTick = timeLimit;
        resume();
    }

    public void cancel() {
        timer.cancel();
        timer.purge();
        executor.shutdownNow();
    }

    // https://stackoverflow.com/a/4685606
    public void setTimerCallBack(Callable endCallback, Callable tickCallback, int _tickInterval) {
        tickInterval = _tickInterval;

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isPaused) {
                    currentTick--;

                    // sau tickInterval giây, sẽ gọi 1 lần tick-callback
                    if (tickCallback != null && (timeLimit - currentTick) % tickInterval == 0) {
                        try {
                            executor.submit(tickCallback);
                        } catch (Exception ex) {
//                            Logger.getLogger(Caro.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    // khi đếm ngược tới 0 sẽ gọi end-callback
                    if (currentTick <= 0) {
                        try {
                            if (endCallback != null) {
                                executor.submit(endCallback);
                            }
                            executor.shutdown();
                        } catch (Exception ex) {
//                            Logger.getLogger(Caro.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        timer.cancel();
                        timer.purge();
                    }
                }
            }
        }, 0, 1000);
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getTickInterval() {
        return tickInterval;
    }

    public void setTickInterval(int tickInterval) {
        this.tickInterval = tickInterval;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }
}