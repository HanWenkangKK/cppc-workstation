CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TABLE IF NOT EXISTS patient_info (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    gender VARCHAR(16),
    age INTEGER,
    disease_type VARCHAR(64),
    lesion_side VARCHAR(32),
    remark VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_patient_info_age CHECK (age IS NULL OR age >= 0)
);

COMMENT ON TABLE patient_info IS 'Patient base information';
COMMENT ON COLUMN patient_info.id IS 'Primary key';
COMMENT ON COLUMN patient_info.name IS 'Patient name';
COMMENT ON COLUMN patient_info.gender IS 'Gender';
COMMENT ON COLUMN patient_info.age IS 'Age';
COMMENT ON COLUMN patient_info.disease_type IS 'Disease type';
COMMENT ON COLUMN patient_info.lesion_side IS 'Lesion side';
COMMENT ON COLUMN patient_info.remark IS 'Remark';
COMMENT ON COLUMN patient_info.created_at IS 'Created time';
COMMENT ON COLUMN patient_info.updated_at IS 'Updated time';


CREATE TABLE IF NOT EXISTS assessment_record (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    chief_complaint VARCHAR(500),
    extra_text TEXT,
    tag_ids_json JSONB,
    image_urls_json JSONB,
    status VARCHAR(32) NOT NULL DEFAULT 'draft',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_assessment_record_patient
        FOREIGN KEY (patient_id) REFERENCES patient_info (id),
    CONSTRAINT chk_assessment_record_status
        CHECK (status IN ('draft', 'generated'))
);

CREATE INDEX IF NOT EXISTS idx_assessment_record_patient_id
    ON assessment_record (patient_id);

COMMENT ON TABLE assessment_record IS 'Patient assessment record';
COMMENT ON COLUMN assessment_record.id IS 'Primary key';
COMMENT ON COLUMN assessment_record.patient_id IS 'Patient id';
COMMENT ON COLUMN assessment_record.chief_complaint IS 'Chief complaint';
COMMENT ON COLUMN assessment_record.extra_text IS 'Additional text';
COMMENT ON COLUMN assessment_record.tag_ids_json IS 'Selected tag ids snapshot';
COMMENT ON COLUMN assessment_record.image_urls_json IS 'Image url list';
COMMENT ON COLUMN assessment_record.status IS 'Status: draft/generated';
COMMENT ON COLUMN assessment_record.created_at IS 'Created time';
COMMENT ON COLUMN assessment_record.updated_at IS 'Updated time';


CREATE TABLE IF NOT EXISTS cppc_tags (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT NOT NULL DEFAULT 0,
    biz_code VARCHAR(64) NOT NULL,
    label VARCHAR(128) NOT NULL,
    level INTEGER NOT NULL,
    sort_no INTEGER NOT NULL DEFAULT 0,
    is_leaf BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_cppc_tags_biz_code UNIQUE (biz_code),
    CONSTRAINT chk_cppc_tags_level CHECK (level > 0)
);

CREATE INDEX IF NOT EXISTS idx_cppc_tags_parent_id
    ON cppc_tags (parent_id);

COMMENT ON TABLE cppc_tags IS 'CPPC assessment tags';
COMMENT ON COLUMN cppc_tags.id IS 'Primary key';
COMMENT ON COLUMN cppc_tags.parent_id IS 'Parent id, 0 means root';
COMMENT ON COLUMN cppc_tags.biz_code IS 'Business code';
COMMENT ON COLUMN cppc_tags.label IS 'Tag label';
COMMENT ON COLUMN cppc_tags.level IS 'Tree level';
COMMENT ON COLUMN cppc_tags.sort_no IS 'Sort number';
COMMENT ON COLUMN cppc_tags.is_leaf IS 'Leaf node flag';
COMMENT ON COLUMN cppc_tags.created_at IS 'Created time';
COMMENT ON COLUMN cppc_tags.updated_at IS 'Updated time';


CREATE TABLE IF NOT EXISTS report_record (
    id BIGSERIAL PRIMARY KEY,
    assessment_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    report_type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'generating',
    content JSONB,
    original_content JSONB,
    is_modified BOOLEAN NOT NULL DEFAULT FALSE,
    ai_trace_id VARCHAR(128),
    error_msg VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_report_record_assessment
        FOREIGN KEY (assessment_id) REFERENCES assessment_record (id),
    CONSTRAINT fk_report_record_patient
        FOREIGN KEY (patient_id) REFERENCES patient_info (id),
    CONSTRAINT chk_report_record_status
        CHECK (status IN ('generating', 'success', 'failed'))
);

CREATE INDEX IF NOT EXISTS idx_report_record_assessment_id
    ON report_record (assessment_id);

CREATE INDEX IF NOT EXISTS idx_report_record_patient_id
    ON report_record (patient_id);

COMMENT ON TABLE report_record IS 'AI report record';
COMMENT ON COLUMN report_record.id IS 'Primary key';
COMMENT ON COLUMN report_record.assessment_id IS 'Assessment record id';
COMMENT ON COLUMN report_record.patient_id IS 'Patient id';
COMMENT ON COLUMN report_record.report_type IS 'Report type';
COMMENT ON COLUMN report_record.status IS 'Status: generating/success/failed';
COMMENT ON COLUMN report_record.content IS 'Current structured content';
COMMENT ON COLUMN report_record.original_content IS 'Original AI structured content';
COMMENT ON COLUMN report_record.is_modified IS 'Manual modification flag';
COMMENT ON COLUMN report_record.ai_trace_id IS 'AI trace id';
COMMENT ON COLUMN report_record.error_msg IS 'Failure reason';
COMMENT ON COLUMN report_record.created_at IS 'Created time';
COMMENT ON COLUMN report_record.updated_at IS 'Updated time';


DROP TRIGGER IF EXISTS trg_patient_info_set_updated_at ON patient_info;
CREATE TRIGGER trg_patient_info_set_updated_at
BEFORE UPDATE ON patient_info
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

DROP TRIGGER IF EXISTS trg_assessment_record_set_updated_at ON assessment_record;
CREATE TRIGGER trg_assessment_record_set_updated_at
BEFORE UPDATE ON assessment_record
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

DROP TRIGGER IF EXISTS trg_cppc_tags_set_updated_at ON cppc_tags;
CREATE TRIGGER trg_cppc_tags_set_updated_at
BEFORE UPDATE ON cppc_tags
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

DROP TRIGGER IF EXISTS trg_report_record_set_updated_at ON report_record;
CREATE TRIGGER trg_report_record_set_updated_at
BEFORE UPDATE ON report_record
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();
