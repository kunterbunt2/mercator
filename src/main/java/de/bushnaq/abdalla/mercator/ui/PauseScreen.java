package de.bushnaq.abdalla.mercator.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import de.bushnaq.abdalla.engine.AbstractPauseScreen;
import de.bushnaq.abdalla.engine.IGameEngine;

public class PauseScreen extends AbstractPauseScreen {
    public PauseScreen(IGameEngine gameEngine, TextureAtlas.AtlasRegion atlasRegion, BitmapFont menuBoldFont, BitmapFont menuFont) {
        super(gameEngine, atlasRegion, menuBoldFont, menuFont);
    }

    protected void initializeKeyboardCommands() {
        // ---------------------------------------------------------
        // Function keys
        // ---------------------------------------------------------
        addCommand("ESC", "Quit", KeyType.GameControl);
        addCommand("F1", "Gamma Correction", KeyType.Debugging);
        addCommand("F2", "Depth of Field", KeyType.Debugging);
        addCommand("F3", "Bokeh", KeyType.Debugging);
        addCommand("F4", "SSAO", KeyType.Debugging);

        addCommand("F5", "Always Day", KeyType.Debugging);
        addCommand("F6", "Demo Modes", KeyType.GameControl);
        // addCommand("F7", "");
        // addCommand("F8", "");

        addCommand("F9", "Graphs", KeyType.Debugging);
        addCommand("F10", "Toggle Debug Mode", KeyType.Debugging);
        // addCommand("F11", "");
        // addCommand("F12", "");

        // ---------------------------------------------------------
        // Numerical keys
        // ---------------------------------------------------------
        // addCommand("`", "Slide Shelton");
        // addCommand("1", "Cockpit Toggle");
        // addCommand("2", "Left Look");
        // addCommand("3", "Right Look");
        // addCommand("4", "Back Look");
        // addCommand("5", "View Chase");
        // addCommand("6", "View Panning");
        // addCommand("7", "View Target");
        // addCommand("8", "View Panning Target");
        // addCommand("9", "TC -");
        // addCommand("0", "TC +");
        // addCommand("-", "Velocity Decrease");
        // addCommand("=", "Velocity Increase");
        // addCommand("Backspace", "Relative Stop");

        // ---------------------------------------------------------
        // First letter row
        // ---------------------------------------------------------
        addCommand("Tab", "Profiler (in Info Panel)", KeyType.Debugging);
        addCommand("Q", "Rotate Camera Left", KeyType.Camera);
        addCommand("W", "Move Camera Forward", KeyType.Camera);
        addCommand("E", "Rotate Camera Right", KeyType.Camera);
        // addCommand("R", "Hostile Near Next");
        // addCommand("T", "Target Next All");
        // addCommand("Y", "Mode Combat Switch");
        // addCommand("U", "Target Manmade Next");
        addCommand("I", "Info Panel", KeyType.UI);
        // addCommand("O", "Turret AI On");
        // addCommand("P", "Boresight Target");
        // addCommand("[", "Ship Change");
        // addCommand("]", "Message Text");
        // addCommand("\\", "Max Velocity");

        // ---------------------------------------------------------
        //- Second letter row
        // ---------------------------------------------------------
        // addCommand("CapsLock", "Caps Lock");
        addCommand("A", "Move Camera Left", KeyType.Camera);
        addCommand("S", "Move Camera Backward", KeyType.Camera);
        addCommand("D", "Move Camera Right", KeyType.Camera);
        addCommand("F", "Follow selected trader", KeyType.Camera);
        // addCommand("G", "");
        addCommand("H", "HRTF", KeyType.Debugging);
        // addCommand("J", "Jump Drive");
        // addCommand("K", "Friend Near Next");
        // addCommand("L", "Target Lock");
        // addCommand(";", "Respawn");
        // addCommand("'", "Quote");
        // addCommand("Enter", "Fire Secondary");

        // ---------------------------------------------------------
        //- Third letter row
        // ---------------------------------------------------------
        // addCommand("LShift", "Left Shift");
        // addCommand("Z", "Up Pan");
        // addCommand("X", "X Key");
        // addCommand("C", "Reset Camera");
        addCommand("V", "VSync", KeyType.Debugging);
        // addCommand("B", "Subunit Next");
        // addCommand("N", "Target Neutral Next");
        // addCommand("M", "Left VDU Cycle");
        // addCommand(",", "Up Thrust");
        // addCommand(".", "Down Thrust");
        // addCommand("/", "Forward Slash");
        // addCommand("RShift", "Right Shift");

        // ---------------------------------------------------------
        //- Forth letter row
        // ---------------------------------------------------------
        // addCommand("LCtrl", "Left Control");
        // addCommand("LWin", "Left Windows");
        // addCommand("LAlt", "Left Alt");
        addCommand("Space", "Debug Pause", KeyType.GameControl);
        // addCommand("RAlt", "Right Alt");
        // addCommand("RWin", "Right Windows");
        // addCommand("Menu", "Menu Key");
        // addCommand("RCtrl", "Right Control");

        // ---------------------------------------------------------
        //-
        // ---------------------------------------------------------

        addCommand("PRNT", "Screenshot", KeyType.GameControl);
        // addCommand("ROLL", "");
        addCommand("PAUSE", "Pause", KeyType.GameControl);

        // addCommand("Insert", "Left Roll");
        // addCommand("Home", "Velocity Match");
        // addCommand("PageUp", "Text Scroll Up");
        // addCommand("Delete", "Right Roll");
        // addCommand("End", "Velocity Zero");
        // addCommand("PageDown", "Text Scroll Down");

        addCommand("Up", "Move Camera Forward", KeyType.Camera);
        addCommand("Down", "Move Camera Backward", KeyType.Camera);
        addCommand("Left", "Move Camera Left", KeyType.Camera);
        addCommand("Right", "Move Camera Right", KeyType.Camera);

    }
}