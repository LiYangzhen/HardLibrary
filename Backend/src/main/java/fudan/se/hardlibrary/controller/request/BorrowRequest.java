package fudan.se.hardlibrary.controller.request;

import lombok.Data;

@Data
public class BorrowRequest {

    private String[] copiesIsbn;
    private String username;
    private String admin;
    private String location;
    private Boolean[] ifDamaged;
    private Boolean[] ifLost;

}
