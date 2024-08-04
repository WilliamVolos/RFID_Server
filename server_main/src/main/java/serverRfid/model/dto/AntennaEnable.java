package serverRfid.model.dto;

import serverRfid.model.constant.AntennaEnum;

public record AntennaEnable(AntennaEnum antName, boolean enable) {
}
