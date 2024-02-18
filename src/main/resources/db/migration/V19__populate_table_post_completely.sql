CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

INSERT INTO public.post (id, created_at, allow_comments, content, post_time, subtitle, title, author_id, cover_image_id)
SELECT
    uuid_generate_v4(),
    '2024-02-17 17:25:08.225217',
    true,
    '<div><p>Conteúdo <strong>HTML</strong> aleatório para o Post </p></div>',
    '2024-02-17 17:25:08.225217',
    'Subtítulo do post',
    'Título do post',
    '8da648d2-625a-40d5-83fe-2fd21c413a01',
    cover_image_id
FROM (
         SELECT id AS cover_image_id
         FROM public.cover_image
--          LIMIT 102
     ) AS cover_images;