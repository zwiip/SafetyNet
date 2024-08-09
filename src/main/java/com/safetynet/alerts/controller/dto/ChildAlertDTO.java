package com.safetynet.alerts.controller.dto;

import java.util.ArrayList;

public class ChildAlertDTO {
    private ArrayList<FullNameAndAgeDTO> childList;
    private ArrayList<FullNameAndAgeDTO> otherMembersList;

    public ChildAlertDTO(ArrayList<FullNameAndAgeDTO> childList, ArrayList<FullNameAndAgeDTO> otherMembersList) {
        this.childList = childList;
        this.otherMembersList = otherMembersList;
    }

    public ArrayList<FullNameAndAgeDTO> getChildList() {
        return childList;
    }

    public ArrayList<FullNameAndAgeDTO> getOtherMembersList() {
        return otherMembersList;
    }
}
