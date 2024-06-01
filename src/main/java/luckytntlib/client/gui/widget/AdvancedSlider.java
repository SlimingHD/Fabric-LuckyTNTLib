package luckytntlib.client.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

/**
 * Slider widget implementation which allows inputting values in a certain range with optional step size.
 */
public class AdvancedSlider extends SliderWidget {
	
    private static final Identifier TEXTURE = new Identifier("widget/slider");
    private static final Identifier HIGHLIGHTED_TEXTURE = new Identifier("widget/slider_highlighted");
    private static final Identifier HANDLE_TEXTURE = new Identifier("widget/slider_handle");
    private static final Identifier HANDLE_HIGHLIGHTED_TEXTURE = new Identifier("widget/slider_handle_highlighted");
	
    protected Text prefix;
    protected Text suffix;

    protected double minValue;
    protected double maxValue;

    /** Allows input of discontinuous values with a certain step */
    protected double stepSize;

    protected boolean drawString;

    private final DecimalFormat format;

    /**
     * @param x x position of upper left corner
     * @param y y position of upper left corner
     * @param width Width of the widget
     * @param height Height of the widget
     * @param prefix {@link Component} displayed before the value string
     * @param suffix {@link Component} displayed after the value string
     * @param minValue Minimum (left) value of slider
     * @param maxValue Maximum (right) value of slider
     * @param currentValue Starting value when widget is first displayed
     * @param stepSize Size of step used. Precision will automatically be calculated based on this value if this value is not 0.
     * @param precision Only used when {@code stepSize} is 0. Limited to a maximum of 4 (inclusive).
     * @param drawString Should text be displayed on the widget
     */
    public AdvancedSlider(int x, int y, int width, int height, Text prefix, Text suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString) {
        super(x, y, width, height, Text.empty(), 0D);
        this.prefix = prefix;
        this.suffix = suffix;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepSize = Math.abs(stepSize);
        value = snapToNearest((currentValue - minValue) / (maxValue - minValue));
        this.drawString = drawString;

        if (stepSize == 0D) {
            precision = Math.min(precision, 4);

            StringBuilder builder = new StringBuilder("0");

            if (precision > 0)
                builder.append('.');

            while (precision-- > 0)
                builder.append('0');

            format = new DecimalFormat(builder.toString());
        } else if (MathHelper.approximatelyEquals(this.stepSize, Math.floor(this.stepSize))) {
            format = new DecimalFormat("0");
        } else {
            format = new DecimalFormat(Double.toString(this.stepSize).replaceAll("\\d", "0"));
        }

        updateMessage();
    }

    /**
     * Overload with {@code stepSize} set to 1, useful for sliders with whole number values.
     */
    public AdvancedSlider(int x, int y, int width, int height, Text prefix, Text suffix, double minValue, double maxValue, double currentValue, boolean drawString) {
        this(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, 1D, 0, drawString);
    }

    /**
     * @return Current slider value as a double
     */
    public double getValue() {
        return this.value * (maxValue - minValue) + minValue;
    }

    /**
     * @return Current slider value as an long
     */
    public long getValueLong() {
        return Math.round(getValue());
    }

    /**
     * @return Current slider value as an int
     */
    public int getValueInt() {
        return (int) getValueLong();
    }

    /**
     * @param value The new slider value
     */
    public void setValue(double value) {
        value = snapToNearest((value - minValue) / (maxValue - minValue));
        updateMessage();
    }

    public String getValueString() {
        return format.format(getValue());
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        setValueFromMouse(mouseX);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        super.onDrag(mouseX, mouseY, dragX, dragY);
        setValueFromMouse(mouseX);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean flag = keyCode == GLFW.GLFW_KEY_LEFT;
        if (flag || keyCode == GLFW.GLFW_KEY_RIGHT) {
            if (minValue > maxValue)
                flag = !flag;
            float f = flag ? -1F : 1F;
            if (stepSize <= 0D)
                setSliderValue(value + (f / (width - 8)));
            else
                setValue(getValue() + f * stepSize);
        }

        return false;
    }

    private void setValueFromMouse(double mouseX) {
        setSliderValue((mouseX - (getX() + 4)) / (width - 8));
    }

    /**
     * @param value Percentage of slider range
     */
    private void setSliderValue(double value) {
        double oldValue = value;
        value = snapToNearest(value);
        if (!MathHelper.approximatelyEquals(oldValue, value))
            applyValue();

        updateMessage();
    }

    /**
     * Snaps the value, so that the displayed value is the nearest multiple of {@code stepSize}.
     * If {@code stepSize} is 0, no snapping occurs.
     */
    private double snapToNearest(double value) {
        if(stepSize <= 0D)
            return MathHelper.clamp(value, 0D, 1D);

        value = MathHelper.lerp(MathHelper.clamp(value, 0D, 1D), minValue, maxValue);

        value = (stepSize * Math.round(value / stepSize));

        if (this.minValue > maxValue)
            value = MathHelper.clamp(value, maxValue, minValue);
        else
            value = MathHelper.clamp(value, minValue, maxValue);

        return MathHelper.map(value, minValue, maxValue, 0D, 1D);
    }

	public int getFGColor() {
		return active ? 16777215 : 10526880;
	}
	
	private Identifier getTexture() {
		Field field;
		boolean slider = false;
		try {
			field = SliderWidget.class.getDeclaredField("sliderFocused");
			field.setAccessible(true);
			slider = field.getBoolean(this);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
        if (this.isFocused() && !slider) {
            return HIGHLIGHTED_TEXTURE;
        }
        return TEXTURE;
    }

    private Identifier getHandleTexture() {
    	Field field;
		boolean slider = false;
		try {
			field = SliderWidget.class.getDeclaredField("sliderFocused");
			field.setAccessible(true);
			slider = field.getBoolean(this);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
    	
        if (this.hovered || slider) {
            return HANDLE_HIGHLIGHTED_TEXTURE;
        }
        return HANDLE_TEXTURE;
    }

    @Override
    protected void updateMessage() {
        if (drawString)
            setMessage(Text.literal("").append(prefix).append(getValueString()).append(suffix));
        else
            setMessage(Text.empty());
    }

    @Override
    protected void applyValue() {}

    @Override
    public void renderWidget(DrawContext guiGraphics, int mouseX, int mouseY, float delta) {
        final MinecraftClient mc = MinecraftClient.getInstance();
        guiGraphics.drawGuiTexture(getTexture(), getX(), getY(), getWidth(), getHeight());
        guiGraphics.drawGuiTexture(getHandleTexture(), getX() + (int)(value * (double)(width - 8)), getY(), 8, getHeight());

        drawScrollableText(guiGraphics, mc.textRenderer, 2, getFGColor() | MathHelper.ceil(alpha * 255.0F) << 24);
    }
}
