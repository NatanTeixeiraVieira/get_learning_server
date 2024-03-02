INSERT INTO public.post_tag (post_id, tag_id)
SELECT
    post_id,
    tag_id
FROM (
         SELECT id AS post_id FROM public.post ORDER BY RANDOM()
     ) AS p,
     (
         SELECT id AS tag_id FROM public.tag ORDER BY RANDOM() LIMIT(FLOOR(RANDOM() * 3) + 1)
     ) AS t;