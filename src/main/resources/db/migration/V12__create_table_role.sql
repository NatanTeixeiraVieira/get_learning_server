-- Table: public.role

-- DROP TABLE IF EXISTS public.role;

CREATE TABLE IF NOT EXISTS public.role
(
    id uuid NOT NULL,
    name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT role_pkey PRIMARY KEY (id),
    CONSTRAINT uk_8sewwnpamngi6b1dwaa88askk UNIQUE (name),
    CONSTRAINT role_name_check CHECK (name::text = ANY (ARRAY['USER'::character varying, 'ADMIN'::character varying, 'SUPER_ADMIN'::character varying]::text[]))
    );

--     TABLESPACE pg_default;
--
-- ALTER TABLE IF EXISTS public.role
--     OWNER to postgres;