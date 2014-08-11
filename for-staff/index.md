---
layout: default
title: Information for Course Staff
---

Lecture Notes
-------------

<ul>
  {% for post in site.lecture_notes %}
    <li>
      <a href="./{{ post.url }}">{{ post.title }}</a>
    </li>
  {% endfor %}
</ul>

Homework
--------

<ul>
  {% for post in site.data.hw %}
    <li>
      <a href="./{{ post.url }}">{{ post.title }}</a>
    </li>
  {% endfor %}
</ul>
