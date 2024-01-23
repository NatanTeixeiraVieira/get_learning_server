-- Table: public.cover_image

-- DROP TABLE IF EXISTS public.cover_image;

CREATE TABLE IF NOT EXISTS public.cover_image
(
    id uuid NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    url character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT cover_image_pkey PRIMARY KEY (id)
    )

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.cover_image
    OWNER to postgres;