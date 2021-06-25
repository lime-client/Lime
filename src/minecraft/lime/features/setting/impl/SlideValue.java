package lime.features.setting.impl;

import lime.features.module.Module;
import lime.features.setting.SettingValue;

public class SlideValue extends SettingValue {
    private final double min, max, increment;
    private double current;

    private ISliderCallBack iSlideSetting;

    public SlideValue(String settingName, Module parentModule, double min, double max, double _default, double increment) {
        super(settingName, parentModule);
        this.min = min;
        this.max = max;
        this.increment = increment;
        this.current = _default;
    }

    public SlideValue(String settingName, Module parentModule, double min, double max, double _default, double increment, ISliderCallBack iSlideSetting) {
        this(settingName, parentModule, min, max, _default, increment);
        this.iSlideSetting = iSlideSetting;
    }

    public void onChange(double old, double _new) {
        if(iSlideSetting != null)
            iSlideSetting.onChange(old, _new);
    }

    public double getCurrent() {
        if(increment == 1) return (int) current;
        return current;
    }

    public double getIncrement() {
        return increment;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public void setCurrentValue(double current) {
        this.current = current;
    }

    public interface ISliderCallBack
    {
        void onChange(double old, double _new);
    }
}
