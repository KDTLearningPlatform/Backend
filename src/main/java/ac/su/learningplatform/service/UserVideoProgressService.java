package ac.su.learningplatform.service;

import ac.su.learningplatform.domain.Video;
import ac.su.learningplatform.dto.VideoDTO.Response;
import ac.su.learningplatform.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserVideoProgressService {

    private final VideoRepository videoRepository;

    @Autowired
    public UserVideoProgressService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public Optional<Response> getVideoById(Long videoId) {
        Optional<Video> videoOptional = videoRepository.findById(videoId);
        if (videoOptional.isPresent()) {
            Video video = videoOptional.get();
            Response videoResponse = new Response();
            videoResponse.setVideoId(video.getVideoId());
            videoResponse.setVideoOrder(video.getVideoOrder());
            videoResponse.setTitle(video.getTitle());
            videoResponse.setContent(video.getContent());
            videoResponse.setRunningTime(video.getRunningTime());
            return Optional.of(videoResponse);
        } else {
            return Optional.empty();
        }
    }
}
