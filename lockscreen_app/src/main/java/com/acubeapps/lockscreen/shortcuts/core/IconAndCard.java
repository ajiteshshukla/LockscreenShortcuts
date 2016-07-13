package com.acubeapps.lockscreen.shortcuts.core;

import com.acubeapps.lockscreen.shortcuts.core.card.Card;
import com.acubeapps.lockscreen.shortcuts.core.icon.Icon;
import com.inmobi.oem.thrift.ad.model.TAd;

public class IconAndCard {
    final TAd ad;
    final Icon icon;
    final Card card;

    public IconAndCard(TAd ad, Icon icon, Card card) {
        this.ad = ad;
        this.icon = icon;
        this.card = card;
    }

    public TAd getAd() {
        return ad;
    }

    public Icon getIcon() {
        return icon;
    }

    public Card getCard() {
        return card;
    }

    @Override
    public String toString() {
        return "IconAndCard{"
                + "ad=" + ad
                + '}';
    }
}
