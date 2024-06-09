package luckytntlib.client.gui;

import luckytntlib.client.gui.widget.AdvancedSlider;
import luckytntlib.client.gui.widget.CenteredStringWidget;
import luckytntlib.config.LuckyTNTLibConfigValues;
import luckytntlib.config.common.Config;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.GridWidget.Adder;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

/**
 * The config screen of Lucky TNT Lib.
 * Extending this is not advised.
 */
public class ConfigScreen extends Screen{

	ButtonWidget performant_explosion = null;
	
	AdvancedSlider explosion_performance_factor_slider = null;
	
	ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this, 20, 40);
	
	public ConfigScreen() {
		super(Text.translatable("config.title"));
	}
	
	@Override
	public void init() {
		DirectionalLayoutWidget linear = layout.addHeader(DirectionalLayoutWidget.vertical());
		linear.add(new TextWidget(Text.translatable("config.title"), textRenderer), Positioner::alignHorizontalCenter);
		GridWidget grid = new GridWidget();
		
		grid.getMainPositioner().marginX(4).marginBottom(4).alignHorizontalCenter();
		Adder rows = grid.createAdder(3);
		rows.add(performant_explosion = new ButtonWidget.Builder(LuckyTNTLibConfigValues.PERFORMANT_EXPLOSION.get().booleanValue() ? ScreenTexts.ON : ScreenTexts.OFF, button -> nextBooleanValue(LuckyTNTLibConfigValues.PERFORMANT_EXPLOSION, button)).width(100).build());
		performant_explosion.setTooltip(Tooltip.of(Text.translatable("config.performant_explosion_tooltip")));
		rows.add(new CenteredStringWidget(Text.translatable("config.performant_explosion"), textRenderer));
		rows.add(new ButtonWidget.Builder(Text.translatable("config.reset"), button -> resetBooleanValue(LuckyTNTLibConfigValues.PERFORMANT_EXPLOSION, performant_explosion)).width(100).build());
		rows.add(explosion_performance_factor_slider = new AdvancedSlider(0, 0, 100, 20, Text.empty(), Text.empty(), 30d, 60d, LuckyTNTLibConfigValues.EXPLOSION_PERFORMANCE_FACTOR.get() * 100, true));
		explosion_performance_factor_slider.setTooltip(Tooltip.of(Text.translatable("config.explosion_performance_factor_tooltip")));
		rows.add(new CenteredStringWidget(Text.translatable("config.explosion_performance_factor"), textRenderer));
		rows.add(new ButtonWidget.Builder(Text.translatable("config.reset"), button -> resetDoubleValue(LuckyTNTLibConfigValues.EXPLOSION_PERFORMANCE_FACTOR, explosion_performance_factor_slider)).width(100).build());
		
		layout.addBody(grid);
		layout.addFooter(new ButtonWidget.Builder(ScreenTexts.DONE, button -> close()).width(100).build());
		layout.forEachChild(this::addDrawableChild);
		initTabNavigation();
	}
	
    @Override
    protected void initTabNavigation() {
        layout.refreshPositions();
    }
	
	@Override
	public void close() {
		if(explosion_performance_factor_slider != null) {
			LuckyTNTLibConfigValues.EXPLOSION_PERFORMANCE_FACTOR.set(explosion_performance_factor_slider.getValue() / 100d);
		}
		if(LuckyTNTLibConfigValues.CONFIG != null) {
			LuckyTNTLibConfigValues.CONFIG.save(client.world);
		}
		super.close();
	}
	
	public void resetDoubleValue(Config.DoubleValue config, AdvancedSlider slider) {
		config.set(config.getDefault());
		slider.setValue(config.getDefault() * 100);
	}
	
	public void resetBooleanValue(Config.BooleanValue config, ButtonWidget button) {
		config.set(config.getDefault());
		button.setMessage(config.getDefault() ? ScreenTexts.ON : ScreenTexts.OFF);
	}
	
	public void nextBooleanValue(Config.BooleanValue config, ButtonWidget button) {
		boolean value = config.get().booleanValue();
		if(value) {
			value = false;
		} else {
			value = true;
		}
		config.set(value);
		button.setMessage(value ? ScreenTexts.ON : ScreenTexts.OFF);
	}
}
