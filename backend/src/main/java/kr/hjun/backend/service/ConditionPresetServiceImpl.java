package kr.hjun.backend.service;

import kr.hjun.backend.dto.ConditionPresetRequest;
import kr.hjun.backend.dto.ConditionPresetResponse;
import kr.hjun.backend.entity.ConditionPreset;
import kr.hjun.backend.entity.User;
import kr.hjun.backend.exception.ChronosException;
import kr.hjun.backend.repository.ConditionPresetRepository;
import kr.hjun.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ConditionPresetServiceImpl implements ConditionPresetService {

    private final ConditionPresetRepository presetRepository;
    private final UserRepository userRepository;

    @Override
    public ConditionPresetResponse createPreset(Long userId, ConditionPresetRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ChronosException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        ConditionPreset preset = ConditionPreset.builder()
                .user(user)
                .name(request.getName())
                .description(request.getDescription())
                .conditionsJson(request.getConditionsJson())
                .build();

        return ConditionPresetResponse.from(presetRepository.save(preset));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConditionPresetResponse> getPresets(Long userId) {
        return presetRepository.findAllByUserId(userId).stream()
                .map(ConditionPresetResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePreset(Long userId, Long presetId) {
        ConditionPreset preset = presetRepository.findById(presetId)
                .filter(p -> p.getUser().getId().equals(userId))
                .orElseThrow(() -> new ChronosException(HttpStatus.NOT_FOUND, "프리셋을 찾을 수 없습니다."));
        presetRepository.delete(preset);
    }
}
