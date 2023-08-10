import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.os.CountDownTimer
import java.lang.ref.WeakReference

class TimerViewModel : ViewModel() {

    private val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
        get() = _currentTime

    private lateinit var countDownTimer: CountDownTimer
    private var startTimeInMillis: Long = 0

    fun startTimer(startTimeInMillis: Long) {
        this.startTimeInMillis = startTimeInMillis
        _currentTime.value = startTimeInMillis
        createCountDownTimer(startTimeInMillis)
        countDownTimer.start()
    }

    fun resetTimer(startTimeInMillis: Long) {
        stopTimer()
        this.startTimeInMillis = startTimeInMillis
        _currentTime.value = startTimeInMillis
        createCountDownTimer(startTimeInMillis)
        countDownTimer.start()
    }

    fun stopTimer() {
        countDownTimer.cancel()

    }

    private fun createCountDownTimer(startTimeInMillis: Long) {
        countDownTimer = object : CountDownTimer(startTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = millisUntilFinished
            }

            override fun onFinish() {
                _currentTime.value = 0

            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }

}