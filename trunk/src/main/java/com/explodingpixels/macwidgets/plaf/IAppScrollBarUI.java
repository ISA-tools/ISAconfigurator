package com.explodingpixels.macwidgets.plaf;

import com.explodingpixels.painter.ImagePainter;
import com.explodingpixels.painter.Painter;
import com.explodingpixels.widgets.ImageBasedJComponent;
import com.explodingpixels.widgets.plaf.*;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

/**
 * Creates an iApp style scroll bar, either horizontal or vertical based on
 * {@link javax.swing.JScrollBar#getOrientation()}.
 * <br>
 * <img src="../../../../../graphics/iAppHorizontalScrollBar.png">
 * <img src="../../../../../graphics/iAppVerticalScrollBar.png">
 */
public class IAppScrollBarUI extends SkinnableScrollBarUI {

    private static boolean fButtonsSeparate = initializeSeparateStatus();

    public IAppScrollBarUI() {
        super(createScrollBarSkinProvider());
    }

    public static ComponentUI createUI(JComponent c) {
        return new IAppScrollBarUI();
    }

    public static boolean areButtonsSeparate() {
        return fButtonsSeparate;
    }

    public static void setButtonsSeparate(boolean buttonsSeparate) {
        IAppScrollBarUI.fButtonsSeparate = buttonsSeparate;
    }

    private static ScrollBarSkinProvider createScrollBarSkinProvider() {
        return new ScrollBarSkinProvider() {
            public ScrollBarSkin provideSkin(ScrollBarOrientation orientation) {
                ScrollBarSkin skin;
                if (fButtonsSeparate) {
                    if (orientation == ScrollBarOrientation.HORIZONTAL) {
                        skin = createHorizontalSeparateSkin();
                    } else {
                        skin = createVerticalSeparateSkin();
                    }
                } else {
                    if (orientation == ScrollBarOrientation.HORIZONTAL) {
                        skin = createHorizontalTogetherSkin();
                    } else {
                        skin = createVerticalTogetherSkin();
                    }
                }
                return skin;
            }
        };
    }

    private static ScrollBarSkin createHorizontalSeparateSkin() {
        Dimension minimumThumbSize = IAppScrollBarArtworkUtils.getHorizontalScrollBarMinimumSize();
        AbstractButton decrementButton = IAppScrollBarArtworkUtils.createHorizontalSeparateDecrementButton();
        AbstractButton incrementButton = IAppScrollBarArtworkUtils.createHorizontalSeparateIncrementButton();
        Painter<Component> trackPainter = new ImagePainter(IAppScrollBarArtworkUtils.getHorizontalTrack().getImage());
        ScrollThumbImagePainter scrollerThumb = IAppScrollBarArtworkUtils.createHorizontalScrollerThumb();
        int decrementButtonRecess = IAppScrollBarArtworkUtils.getScrollBarTopCapRecess();
        int incrementButtonRecess = IAppScrollBarArtworkUtils.getDecrementButtonRecess();
        Dimension preferredSize = new Dimension(100, decrementButton.getPreferredSize().height);

        return new ButtonsSeparateScrollBarSkin(decrementButton, incrementButton, trackPainter, scrollerThumb,
                decrementButtonRecess, incrementButtonRecess, minimumThumbSize, preferredSize);
    }

    private static ScrollBarSkin createVerticalSeparateSkin() {
        Dimension minimumThumbSize = IAppScrollBarArtworkUtils.getVerticalScrollBarMinimumSize();
        AbstractButton decrementButton = IAppScrollBarArtworkUtils.createVerticalSeparateDecrementButton();
        AbstractButton incrementButton = IAppScrollBarArtworkUtils.createVerticalSeparateIncrementButton();
        Painter<Component> trackPainter = new ImagePainter(IAppScrollBarArtworkUtils.getVerticalTrack().getImage());
        ScrollThumbImagePainter scrollerThumb = IAppScrollBarArtworkUtils.createVerticalScrollerThumb();
        int decrementButtonRecess = IAppScrollBarArtworkUtils.getScrollBarTopCapRecess();
        int incrementButtonRecess = IAppScrollBarArtworkUtils.getDecrementButtonRecess();
        Dimension preferredSize = new Dimension(decrementButton.getPreferredSize().width, 100);

        return new ButtonsSeparateScrollBarSkin(decrementButton, incrementButton, trackPainter, scrollerThumb,
                decrementButtonRecess, incrementButtonRecess, minimumThumbSize, preferredSize);
    }

    private static ScrollBarSkin createHorizontalTogetherSkin() {
        JComponent topCap = new ImageBasedJComponent(IAppScrollBarArtworkUtils.getScrollBarLeftCap().getImage());

        Dimension minimumThumbSize = IAppScrollBarArtworkUtils.getHorizontalScrollBarMinimumSize();
        AbstractButton decrementButton = IAppScrollBarArtworkUtils.createHorizontalTogetherDecrementButton();
        AbstractButton incrementButton = IAppScrollBarArtworkUtils.createHorizontalTogetherIncrementButton();
        Painter<Component> trackPainter = new ImagePainter(IAppScrollBarArtworkUtils.getHorizontalTrack().getImage());
        ScrollThumbImagePainter scrollerThumb = IAppScrollBarArtworkUtils.createHorizontalScrollerThumb();
        int topCapRecess = IAppScrollBarArtworkUtils.getScrollBarTopCapRecess();
        int decrementButtonRecess = IAppScrollBarArtworkUtils.getDecrementButtonRecess();
        Dimension preferredSize = new Dimension(100, decrementButton.getPreferredSize().height);

        return new ButtonsTogetherScrollBarSkin(
                topCap, decrementButton, incrementButton, trackPainter, scrollerThumb,
                topCapRecess, decrementButtonRecess, minimumThumbSize, preferredSize);
    }

    private static ScrollBarSkin createVerticalTogetherSkin() {
        Image topCapImage = IAppScrollBarArtworkUtils.getScrollBarTopCap().getImage();
        JComponent topCap = new ImageBasedJComponent(topCapImage);

        Dimension minimumThumbSize = IAppScrollBarArtworkUtils.getVerticalScrollBarMinimumSize();
        AbstractButton decrementButton = IAppScrollBarArtworkUtils.createVerticalTogetherDecrementButton();
        AbstractButton incrementButton = IAppScrollBarArtworkUtils.createVerticalTogetherIncrementButton();
        Painter<Component> trackPainter = new ImagePainter(IAppScrollBarArtworkUtils.getVerticalTrack().getImage());
        ScrollThumbImagePainter scrollerThumb = IAppScrollBarArtworkUtils.createVerticalScrollerThumb();
        int topCapRecess = IAppScrollBarArtworkUtils.getScrollBarTopCapRecess();
        int decrementButtonRecess = IAppScrollBarArtworkUtils.getDecrementButtonRecess();
        Dimension preferredSize = new Dimension(decrementButton.getPreferredSize().width, 100);

        return new ButtonsTogetherScrollBarSkin(
                topCap, decrementButton, incrementButton, trackPainter, scrollerThumb,
                topCapRecess, decrementButtonRecess, minimumThumbSize, preferredSize);
    }

    /**
     * In practice we should initialize this from the AppleScrollBarVariant
     * property in $HOME/Library/Preferences/.GlobalPreferences.plist, which
     * will be either Single (for separate) or DoubleMax (for together).
     *
     * @return <code>true</code> if buttons should be separate,
     *         <code>false</code> if buttons should be placed together at right
     *         or bottom.
     */
    private static boolean initializeSeparateStatus() {
        return false;
    }
}
