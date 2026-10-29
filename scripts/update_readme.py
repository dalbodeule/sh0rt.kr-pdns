#!/usr/bin/env python3
"""Refresh the generated README section from the root OpenAPI document."""

import json
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
START = "<!-- BEGIN GENERATED API -->"
END = "<!-- END GENERATED API -->"
METHODS = ("get", "post", "put", "patch", "delete")


def schema_type(schema):
    if not schema:
        return "—"
    if "$ref" in schema:
        name = schema["$ref"].rsplit("/", 1)[-1]
        return f"[{name}](#{name.lower()})"
    if "oneOf" in schema:
        return " or ".join(schema_type(part) for part in schema["oneOf"])
    kind = schema.get("type", "object")
    if isinstance(kind, list):
        return " or ".join(
            f"array of {schema_type(schema.get('items'))}" if part == "array" else part
            for part in kind
        )
    if kind == "array":
        return f"array of {schema_type(schema.get('items'))}"
    return kind


def media_schema(content):
    return schema_type(content.get("application/json", {}).get("schema"))


def cell(value):
    return str(value or "—").replace("|", "\\|").replace("\n", " ")


def generate(spec):
    lines = [
        "Generated from [openapi.json](openapi.json). Do not edit this section by hand.",
        "",
        "### Endpoints",
        "",
        "| Method | Path | Summary | Request body | Success response |",
        "| --- | --- | --- | --- | --- |",
    ]

    for path, operations in sorted(spec["paths"].items()):
        for method in METHODS:
            operation = operations.get(method)
            if not operation:
                continue
            request = media_schema(operation.get("requestBody", {}).get("content", {}))
            responses = operation.get("responses", {})
            success_code = next((code for code in sorted(responses) if code.startswith("2")), None)
            response = responses.get(success_code, {})
            result = media_schema(response.get("content", {}))
            lines.append(
                f"| `{method.upper()}` | `{path}` | {cell(operation.get('summary'))} | "
                f"{cell(request)} | {cell(success_code)}: {cell(result)} |"
            )

    lines.extend(["", "### Schemas", ""])
    for name, schema in sorted(spec.get("components", {}).get("schemas", {}).items()):
        lines.extend([f"#### {name}", ""])
        if schema.get("description"):
            lines.extend([schema["description"], ""])
        properties = schema.get("properties", {})
        if not properties:
            lines.extend([f"Type: {schema_type(schema)}", ""])
            continue
        required = set(schema.get("required", []))
        lines.extend(["| Field | Type | Required | Description |", "| --- | --- | --- | --- |"])
        for field, definition in properties.items():
            lines.append(
                f"| `{field}` | {cell(schema_type(definition))} | "
                f"{'Yes' if field in required else 'No'} | {cell(definition.get('description'))} |"
            )
        lines.append("")

    return "\n".join(lines).rstrip() + "\n"


def main():
    spec = json.loads((ROOT / "openapi.json").read_text(encoding="utf-8"))
    readme_path = ROOT / "README.md"
    readme = readme_path.read_text(encoding="utf-8")
    if readme.count(START) != 1 or readme.count(END) != 1:
        raise ValueError("README.md must contain one pair of generated API markers")
    before, remainder = readme.split(START, 1)
    _, after = remainder.split(END, 1)
    readme_path.write_text(before + START + "\n" + generate(spec) + END + after, encoding="utf-8")


if __name__ == "__main__":
    main()
