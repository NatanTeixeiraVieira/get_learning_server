-- Table: public.tag

-- DROP TABLE IF EXISTS public.tag;

CREATE TABLE IF NOT EXISTS public.tag
(
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    name character varying(255) COLLATE pg_catalog."default",
    slug character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT tag_pkey PRIMARY KEY (id)
    )

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.tag
    OWNER to postgres;