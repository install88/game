package com.example.demo.vo;


import java.util.List;

public class RoomVO {
	private String id;
	private String no;
	private String admID;
	private String ownerID;
	private String status;
	private String template;
	private List<GameVO> games;
	public RoomVO() {
		
	}
	public RoomVO(String id, String no, String status) {
		super();
		this.id = id;
		this.no = no;
		this.status = status;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<GameVO> getGames() {
		return games;
	}
	public void setGames(List<GameVO> games) {
		this.games = games;
	}
	public String getAdmID() {
		return admID;
	}
	public void setAdmID(String admID) {
		this.admID = admID;
	}
	public String getOwnerID() {
		return ownerID;
	}
	public void setOwnerID(String ownerID) {
		this.ownerID = ownerID;
	}
	public void addGame(GameVO vo) {
		this.games.add(vo);
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public GameVO getLastGame() {
		return (this.games==null|| this.games.isEmpty())?null:this.games.get(this.games.size()-1);
	}
}
