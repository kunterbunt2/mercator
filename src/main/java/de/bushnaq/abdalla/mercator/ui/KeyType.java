package de.bushnaq.abdalla.mercator.ui;

import com.badlogic.gdx.graphics.Color;
import de.bushnaq.abdalla.engine.util.ColorUtil;
import lombok.Getter;

public enum KeyType {
    Combat(ColorUtil.srgbToLinear(new Color(0xFC9C93FF))),
    UI(ColorUtil.srgbToLinear(new Color(0x0CC9D9FF))),
    Targeting(ColorUtil.srgbToLinear(new Color(0xCEB46CFF))),
    Camera(ColorUtil.srgbToLinear(new Color(0x80B9FFFF))),
    Navigation(ColorUtil.srgbToLinear(new Color(0x7CC690FF))),
    GameControl(ColorUtil.srgbToLinear(new Color(0xE4A0DBFF))),//f3d0ee
    Debugging(ColorUtil.srgbToLinear(new Color(0x880000FF)));
    @Getter
    private final Color color;

    KeyType(Color color) {
        this.color = color;
    }
}
