package com.festivaltime.festivaltimeproject.badgedatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "badges")
public class BadgeEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "image_path")
    public String imagePath;

    @ColumnInfo(name = "badge_name")
    public String badgeName;

    public BadgeEntity() {
        // Room에 필요한 빈 생성자
    }

    public BadgeEntity(String imagePath, String badgeName) {
        this.imagePath = imagePath;
        this.badgeName = badgeName;
    }
}
