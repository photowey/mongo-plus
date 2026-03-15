# DSL Design

## Overview

The DSL module provides the canonical intermediate representation and shared fluent contracts for
query construction in MongoPlus. It does not execute queries and it does not depend on Spring Data
execution APIs. Instead, it defines the fluent contracts and the AST structure that wrappers build
and compilers later consume.

The current implementation centers on `ConditionDSL`, `QueryDSL`, and an AST made of root,
logical, condition, and geo nodes.

## Primary Types

| Type | Role |
|------|------|
| `ConditionDSL<T, R>` | Shared condition-building contract for wrapper implementations |
| `QueryDSL<T, R>` | Shared projection, sort, and pagination configuration contract |
| `Operator` | Normalized operator set used by AST nodes and compilers |
| `AstNode` | Base AST contract |
| `RootNode` | Single-entry AST root |
| `LogicalNode` | Composite node for `AND`, `OR`, and `NOT` |
| `ConditionNode` | Leaf node for a field/operator/value condition |
| `NearNode` / `GeoWithinNode` / `GeoIntersectsNode` | Geo-specific AST nodes |
| `AstVisitor<R>` | Visitor contract for compilation or analysis |

## What The DSL Owns

The DSL layer owns:

- condition operator vocabulary
- query-shaping API contracts
- AST node hierarchy
- visitor extension points
- metadata-aware default overloads on `ConditionDSL` and `QueryDSL`

The DSL layer does not own:

- mutable wrapper state storage
- `MongoTemplate` access
- query compilation output types such as `Query` or `Aggregation`

## AST Shape

The AST is intentionally small and compositional:

```text
RootNode
└── LogicalNode(AND | OR | NOT)
    ├── ConditionNode
    ├── ConditionNode
    └── LogicalNode(...)
```

Key properties:

- `RootNode` can hold only one child.
- `LogicalNode` is the composite container for boolean structure.
- `ConditionNode` is a leaf and cannot accept children.
- Geo predicates use dedicated node types so visitors can distinguish them from scalar operators.

## Condition Contract

`ConditionDSL` defines the reusable predicate API used by query and update wrappers. It supports
three field-addressing styles:

- raw string field names
- lambda field references
- `FieldMetadata` objects

`QueryDSL` complements that contract with:

- projection include / exclude
- sort definition
- `limit(long)` / `skip(long)`
- page-based pagination configuration through `paginate(long, long)`

Together, these two interfaces express the fluent query-building surface without forcing query
shaping into a condition-only contract.

## Metadata Support

`ConditionDSL` includes default overloads for `FieldMetadata`. Those methods resolve the field path
through a shared helper and then forward to the raw string-based methods. This keeps metadata-based
queries consistent with lambda and raw-string queries.

## Visitor Boundary

The visitor contract is the hand-off point between the DSL layer and compiler layers. In the
current implementation:

- wrappers build AST nodes
- `AstCompiler` visits the AST and produces Spring Data `Criteria`
- AST nodes can also produce BSON documents directly through `toBson()`

This allows the AST to serve both executable compilation and lower-level inspection/debugging use
cases.

## Design Notes

- The DSL focuses on shared query-building contracts and condition modeling, not execution.
- `QueryDSL` defines the public query-shaping contract, while wrappers still own the mutable state
  for projection, sort, `skip`, and `limit`.
- The operator set is intentionally shared across different higher-level usage styles so wrappers
  and compilers do not drift in semantics.

## Related Files

- [README.md](../README.md)
- [docs/wrapper-design.md](wrapper-design.md)
- [docs/executor-design.md](executor-design.md)
- [ConditionDSL.java](../mongo-plus-dsl/src/main/java/io/github/photowey/mongoplus/dsl/condition/ConditionDSL.java)
- [QueryDSL.java](../mongo-plus-dsl/src/main/java/io/github/photowey/mongoplus/dsl/support/QueryDSL.java)
- [Operator.java](../mongo-plus-dsl/src/main/java/io/github/photowey/mongoplus/dsl/ast/enums/Operator.java)
- [RootNode.java](../mongo-plus-dsl/src/main/java/io/github/photowey/mongoplus/dsl/ast/node/RootNode.java)
- [LogicalNode.java](../mongo-plus-dsl/src/main/java/io/github/photowey/mongoplus/dsl/ast/node/LogicalNode.java)
- [ConditionNode.java](../mongo-plus-dsl/src/main/java/io/github/photowey/mongoplus/dsl/ast/node/ConditionNode.java)
- [AstVisitor.java](../mongo-plus-dsl/src/main/java/io/github/photowey/mongoplus/dsl/ast/visitor/AstVisitor.java)
