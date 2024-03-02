CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DO $$
BEGIN
FOR i IN 1..200 LOOP

INSERT INTO public.tag (id, created_at, updated_at, name, slug)
VALUES (uuid_generate_v4(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Tag ' || i, 'tag-' || i);
END LOOP;
END $$;