-- QueryRepository_countByStrStartsWith.sql

SELECT COUNT(*)
FROM samples

WHERE str LIKE concat(/*str*/'abc', '%')
