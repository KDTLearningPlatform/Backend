package ac.su.learningplatform.dto;

import ac.su.learningplatform.domain.UserVideoProgress;
import ac.su.learningplatform.domain.UserVideoProgressId;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserVideoProgressDTO {
    private Long userId;
    private Long videoId;
    private int lastPlaybackPosition;
    private float progress;

    public static UserVideoProgressDTO fromEntity(UserVideoProgress entity) {
        UserVideoProgressDTO dto = new UserVideoProgressDTO();
        dto.setUserId(entity.getUser().getUserId());
        dto.setVideoId(entity.getVideo().getVideoId());
        dto.setLastPlaybackPosition(entity.getLastPlaybackPosition());
        dto.setProgress(entity.getProgress());
        return dto;
    }

    public UserVideoProgress toEntity() {
        UserVideoProgress entity = new UserVideoProgress();
        UserVideoProgressId id = new UserVideoProgressId(this.userId, this.videoId);
        entity.setId(id);
        entity.setLastPlaybackPosition(this.lastPlaybackPosition);
        entity.setProgress(this.progress);
        return entity;
    }
}
