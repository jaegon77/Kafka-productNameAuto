# Kafka Duplicate & Loss Prevention Project

이 프로젝트는 Kafka 기반 시스템에서 중복 및 메시지 유실을 방지하기 위한 최적화 전략을 구현하는 것을 목표로 합니다. 상품 생성/수정 이벤트를 자동으로 처리하고, 데이터베이스에 중복 없이 안정적으로 저장하기 위해 여러 방면으로 고민하고 설계하였습니다.

## 주요 고민 및 설계 포인트

- **Producer 최적화**
  - **Idempotence 활성화:**  
    `enable.idempotence=true`를 설정하여, 네트워크 장애나 재시도 상황에서도 중복 메시지 전송을 방지함.
  - **전송 신뢰성:**  
    `acks=all`과 적절한 재시도(retries) 설정을 통해 모든 복제본이 응답해야 전송 완료로 간주.
  - **배치 및 압축:**  
    32KB 배치 사이즈와 Snappy 압축을 사용해 메시지 전송 효율 및 네트워크 사용량 최적화.

- **Consumer 최적화**
  - **메시지 유실 방지:**  
    `isolation.level=read_committed` 설정으로, 커밋된 메시지만 처리.
  - **수동 오프셋 커밋:**  
    최대 1000개의 메시지를 한 번에 poll하고, 수동 커밋을 통해 마지막 처리 이후부터 재처리하도록 관리.
  - **리밸런싱 최적화:**  
    Cooperative Rebalancing을 적용하여 파티션 재할당 시 기존 할당을 최대한 유지.
  - **데이터베이스 Upsert:**  
    (pdNo, attrBaseId, attrValId) 복합 Unique Key 기반 upsert 로직으로 중복 데이터 삽입 방지.

- **실패 처리**
  - 처리 실패 메시지는 최대 3회 재시도 후 Dead Letter Topic(DLT)으로 이동, DLT에 쌓인 메시지는 일배치로 재처리.

## 시스템 구성 및 테스트

- **아키텍처:**  
  기본 Producer-Consumer 구조 위에 Kafka 설정과 데이터베이스 upsert 로직을 결합하여, 중복 및 유실 없는 데이터 처리를 구현.
  
- **부하 테스트:**  
  nGrinder를 활용한 부하 테스트를 통해, Producer 로그 상의 중복 UQ 오류는 발생하지만 최종 DB에는 중복 없이 정상 삽입되는 결과를 확인함.

## 결론

이 프로젝트는 Kafka의 idempotence, 수동 오프셋 커밋, Cooperative Rebalancing, 그리고 DB upsert를 통합하여, 고부하 환경에서도 데이터 중복과 메시지 유실을 효과적으로 방지하는 솔루션을 제공합니다.
