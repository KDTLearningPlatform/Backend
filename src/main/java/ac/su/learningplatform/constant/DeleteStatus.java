package ac.su.learningplatform.constant;

// 튜플의 삭제 상태를 나타내는 Enum
public enum DeleteStatus {
    ACTIVE, // 활성
    DELETED // 삭제
}

// 삭제 여부 칼럼 대신 삭제된 데이터만 저장한 테이블을 별도로 생성해 관리할 수도 있음.
// User 의 경우 별도 백업 테이블 생성 ???
