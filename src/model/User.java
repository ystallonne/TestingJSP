package model;

import java.sql.Date;
import java.util.ArrayList;

public class User {
	private String userID;
	private String name;
	private String lastname;
	private String nickname;
	private boolean isBannedFromSystem;
	private String comment; // Comment regarding the reason why user was banned from the system.
	private boolean isActive;
	private boolean isBanned; // User is banned from a class-section.
	private ArrayList settings; // Auto start audio; auto start video; show language selector; default language.
	private Date lastLogin;
	private boolean isLDAP;
	private String userType;
	private String userLevel;
}
