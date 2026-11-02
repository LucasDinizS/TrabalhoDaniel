package market;

public class CategoryTree {
    private static class TreeNode {
        private final String name;
        private final Product product;
        private TreeNode firstChild;
        private TreeNode nextSibling;

        private TreeNode(String name, Product product) {
            this.name = name;
            this.product = product;
        }

        private boolean isProduct() {
            return product != null;
        }
    }

    private final TreeNode root;

    public CategoryTree(String rootName) {
        root = new TreeNode(rootName, null);
    }

    public void insert(String departmentName, Product product) {
        TreeNode department = findOrCreateChild(root, departmentName);
        TreeNode category = findOrCreateChild(department, product.getCategory());
        addChildSorted(category, new TreeNode(product.getName(), product));
    }

    public void print() {
        printNode(root, 0);
        System.out.printf("Altura da árvore: %d | Departamentos: %d | Produtos: %d%n",
                height(root), countChildren(root), countProducts(root));
    }

    public boolean printDepartment(String departmentName) {
        TreeNode department = findChild(root, departmentName);
        if (department == null) {
            return false;
        }
        printNode(department, 0);
        System.out.printf("Produtos no departamento: %d%n", countProducts(department));
        return true;
    }

    public void printDepartmentNames() {
        TreeNode current = root.firstChild;
        while (current != null) {
            System.out.println("  - " + current.name);
            current = current.nextSibling;
        }
    }

    private void printNode(TreeNode node, int depth) {
        String indentation = "    ".repeat(depth);
        if (node.isProduct()) {
            System.out.println(indentation + "• " + node.product);
        } else {
            System.out.println(indentation + node.name.toUpperCase());
        }
        TreeNode child = node.firstChild;
        while (child != null) {
            printNode(child, depth + 1);
            child = child.nextSibling;
        }
    }

    private int countProducts(TreeNode node) {
        int total = node.isProduct() ? 1 : 0;
        TreeNode child = node.firstChild;
        while (child != null) {
            total += countProducts(child);
            child = child.nextSibling;
        }
        return total;
    }

    private int height(TreeNode node) {
        int highest = 0;
        TreeNode child = node.firstChild;
        while (child != null) {
            highest = Math.max(highest, 1 + height(child));
            child = child.nextSibling;
        }
        return highest;
    }

    private int countChildren(TreeNode node) {
        int count = 0;
        TreeNode child = node.firstChild;
        while (child != null) {
            count++;
            child = child.nextSibling;
        }
        return count;
    }

    private TreeNode findChild(TreeNode parent, String name) {
        TreeNode child = parent.firstChild;
        while (child != null) {
            if (!child.isProduct() && child.name.equalsIgnoreCase(name)) {
                return child;
            }
            child = child.nextSibling;
        }
        return null;
    }

    private TreeNode findOrCreateChild(TreeNode parent, String name) {
        TreeNode existing = findChild(parent, name);
        if (existing != null) {
            return existing;
        }
        TreeNode created = new TreeNode(name, null);
        addChildSorted(parent, created);
        return created;
    }

    private void addChildSorted(TreeNode parent, TreeNode node) {
        if (parent.firstChild == null || node.name.compareToIgnoreCase(parent.firstChild.name) < 0) {
            node.nextSibling = parent.firstChild;
            parent.firstChild = node;
            return;
        }
        TreeNode current = parent.firstChild;
        while (current.nextSibling != null && current.nextSibling.name.compareToIgnoreCase(node.name) <= 0) {
            current = current.nextSibling;
        }
        node.nextSibling = current.nextSibling;
        current.nextSibling = node;
    }
}
