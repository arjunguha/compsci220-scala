---
layout: default
title: Information for Course Staff
---

This page has links to all course content, but is not shared with the students
in the class. It is not a big deal if students see this page, but keeping it
confidential will give us more flexibility.

Lecture Notes
-------------

This is for course staff and may not be intelligible to students:

<ul>
  {% for post in site.lecture_notes %}
    <li>
      <a href="../{{ post.url }}">{{ post.title }}</a>
    </li>
  {% endfor %}
</ul>

Reading
-------

<ul>
{% for item in site.data.reading %}

  <li><a href="../reading/{{ item.path }}">{{ item.name }}</a></li>

{% endfor %}
</ul>


Schedule
--------

<a href="../schedule/">Course Schedule</a>

Homework
--------

<ul>
  {% for post in site.data.hw %}
    <li>
      <a href="../hw/{{ post.url }}">{{ post.title }}</a>
    </li>
  {% endfor %}
</ul>
