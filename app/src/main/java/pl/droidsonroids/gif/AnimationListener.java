package pl.droidsonroids.gif;

/**
 * 在特定的动画事件发生时，可以用来运行某些代码的接口。
 */
public interface AnimationListener {
    /**
     * Called when a single loop of the animation is completed.
     */
    public void onAnimationCompleted();
}
