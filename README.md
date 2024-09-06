# Opinion Spam Detection using Knowledge-Based Ontology

## Overview

In the age of social media, users often rely on opinions and reviews to make purchasing decisions. However, the presence of fake reviews, intended to either promote or damage the reputation of products, complicates this process. This project introduces a new approach for detecting opinion spam with high accuracy, leveraging knowledge-based Ontology.

**Keywords:** Opinion spam, Fake review, E-commercial, Ontology.

## Introduction

E-commerce websites increasingly feature user reviews of products and services, which are critical for decision-making. Unfortunately, review spam has become a significant concern, akin to web, email, and blog spam. This paper discusses various types of spam reviews and proposes a model for identifying them.

### Types of Spam Reviews

1. **Non-reviewed Reviews:** 
   - Comments lacking meaningful opinions or those acting as advertisements.

2. **Brand-only Reviews:**
   - Reviews focusing solely on the brand or supplier without evaluating the product itself.

3. **Untruthful Reviews:**
   - Reviews that are either overly positive or negative with the intent to deceive.

4. **Off-topic Reviews:**
   - Reviews that discuss unrelated products or brands.

## Knowledge Base

### Ontology and OWL

Ontology is a data model used to represent concepts and their relationships. It helps in understanding the domain knowledge in a structured format. OWL (Web Ontology Language) extends RDF to provide a framework for creating and sharing ontologies over the web.

### POS Tagging and Grammar Parsing

POS tagging identifies parts of speech within text, essential for natural language processing. Grammar parsing analyzes sentence structures to aid in text understanding.

## Proposed Model

### Ontology Model

Our ontology model focuses on identifying spam reviews by classifying product information into categories like Component/Feature, Style, Origin, and PopularName.

### Preprocessing Module

The preprocessing module is responsible for:
1. **Entities Building:** Extracting entities from the knowledge base.
2. **Normalizing:** Standardizing review data.
3. **Word Splitting and Grammar Parsing:** Using n-gram models and POS tagging to process text.
4. **Entities Identifying:** Recognizing and classifying entities in reviews.

### Opinion Spam Detection Module

The detection module classifies reviews into different types of spam:
1. **Non-review Detection:** Identifies patterns and word ratios to classify non-reviews.
2. **Brand-only Review Detection:** Counts and analyzes brand-related entities.
3. **Off-topic Review Detection:** Determines if reviews discuss unrelated products or brands.
4. **Untruthful Review Detection:** Assesses opinion polarization and other indicators of deceit.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Reference

For more information on this research, you can refer to this [paper](https://journalhcmue.edu.vn/index.php/hcmuejos/article/viewFile/823/814).

