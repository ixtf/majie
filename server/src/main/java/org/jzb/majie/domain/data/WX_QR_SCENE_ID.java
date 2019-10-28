package org.jzb.majie.domain.data;

/**
 * @author jzb 2019-10-24
 */
public enum WX_QR_SCENE_ID {
    TASK_CHARGE_INVITE(1),
    TASK_FOLLOW_INVITE(2);

    private final int scene_id;

    WX_QR_SCENE_ID(int scene_id) {
        this.scene_id = scene_id;
    }

    public int scene_id() {
        return scene_id;
    }

}
